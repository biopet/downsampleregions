package nl.biopet.tools.downsampleregions

import htsjdk.samtools.fastq.{AsyncFastqWriter, BasicFastqWriter, FastqReader}
import htsjdk.samtools.SamReaderFactory
import nl.biopet.utils.ngs.intervals.BedRecordList
import nl.biopet.utils.tool.ToolCommand

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.util.Random

object DownsampleRegions extends ToolCommand[Args] {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    logger.info("Start")

    require(cmdArgs.bamFile.exists(), s"Bam file does not exist: ${cmdArgs.bamFile}")
    require(cmdArgs.bedFile.exists(), s"Bed file does not exist: ${cmdArgs.bedFile}")
    require(cmdArgs.intputR1.exists(), s"Input R1 file does not exist: ${cmdArgs.intputR1}")
    cmdArgs.intputR2.foreach(file => require(file.exists(), s"Input R2 file does not exist: $file"))

    Random.setSeed(cmdArgs.seed)

    val regions = BedRecordList.fromFile(cmdArgs.bedFile)
    require(regions.allRecords.nonEmpty, "Bed file is empty")
    regions.allRecords.foreach(region => require(region.score.isDefined, "Region does not have a score/fraction"))
    regions.allRecords.foreach(region => require(region.score.get >= 0.0 || region.score.get < 1.0, "Region score/fraction should be between 0.0 and 1.0"))
    require(regions.squishBed().length == regions.length, "Regions are overlapping, this is not allowed")

    val bamReader = SamReaderFactory.makeDefault.open(cmdArgs.bamFile)
    regions.validateContigs(bamReader.getFileHeader.getSequenceDictionary)

    val paired = {
      val it = bamReader.iterator()
      val firstRecord = it.next()
      it.close()
      firstRecord.getReadPairedFlag
    }
    require(paired == cmdArgs.intputR2.isDefined, "Bam contains paired reads but input R2 is not defined")
    require(paired == cmdArgs.outputR2.isDefined, "Bam contains paired reads but output R2 is not defined")

    var totalReads = 0L
    val removeIds: mutable.Set[String] = mutable.Set()
    for (region <- regions.allRecords) {
      val fraction = region.score.get + (cmdArgs.deviation * (Random.nextDouble() - 0.5))
      val bamIt = bamReader.query(region.chr, region.start, region.end, false)
      for (samRecord <- bamIt) {
        def removeRead() = {
          totalReads += 1
          val remove = Random.nextDouble() <= fraction
          if (remove) {
            removeIds += samRecord.getReadName
          }
        }
        if (paired) {
          if (!samRecord.isSecondaryOrSupplementary && samRecord.getFirstOfPairFlag) removeRead()
        } else removeRead()
      }
      bamIt.close()
    }
    bamReader.close()

    logger.info(s"Found $totalReads reads")
    logger.info(s"Will remove ${removeIds.size} reads")

    val readerR1 = new FastqReader(cmdArgs.intputR1)
    val writerR1 = new AsyncFastqWriter(new BasicFastqWriter(cmdArgs.outputR1), 1000)

    readerR1.iterator()
      .filter(r => !removeIds.contains(r.getReadName.split(" ").head.stripSuffix("/1")))
      .foreach(writerR1.write)
    readerR1.close()
    writerR1.close()

    if (paired) {
      val readerR2 = new FastqReader(cmdArgs.intputR2.get)
      val writerR2 = new AsyncFastqWriter(new BasicFastqWriter(cmdArgs.outputR2.get), 1000)

      readerR2.iterator()
        .filter { r =>
          !removeIds.contains(r.getReadName.split(" ").head.stripSuffix("/2"))
        }
        .foreach(writerR2.write)
      readerR2.close()
      writerR2.close()
    }

    logger.info("Done")
  }

  def argsParser = new ArgsParser(toolName)

  def emptyArgs = Args()

  def descriptionText: String =
    """
      | This tool can be used to downsample specific regions.
      | Each region can have it own fraction to downsample.
      |
      | All other reads will not be touched.
    """.stripMargin

  def manualText: String =
    """
      | To run this tool a bam file and a bed file is required. The bed file should be formatted like this:
      | <contig> <start> <end> <name> <fraction>
      |
      | By setting --deviation the fraction can deviate from it randomly.
    """.stripMargin

  def exampleText: String =
    """
      | Single end example:
      | java -jar <tool_jar> --bamFile <bam file> --bedFile <bed file> --inputR1 <input R1 fastq> --outputR1 <output R1 fastq>
      |
      | Paired end example:
      | java -jar <tool_jar> --bamFile <bam file> --bedFile <bed file> --inputR1 <input R1 fastq> --inputR2 <input R2 fastq> --outputR1 <output R1 fastq> --outputR2 <output R2 fastq>
    """.stripMargin
}
