package nl.biopet.tools.downsampleregions

import java.io.{File, PrintWriter}

import htsjdk.samtools.fastq.FastqReader
import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

import scala.collection.JavaConversions._

class DownsampleRegionsTest extends ToolTest[Args] {

  def toolCommand: DownsampleRegions.type = DownsampleRegions

  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array())
    }
  }

  @Test
  def testPairedEnd(): Unit = {
    val outputR1A = File.createTempFile("test.", ".fq.gz")
    outputR1A.deleteOnExit()
    val outputR2A = File.createTempFile("test.", ".fq.gz")
    outputR2A.deleteOnExit()
    val outputR1B = File.createTempFile("test.", ".fq.gz")
    outputR1B.deleteOnExit()
    val outputR2B = File.createTempFile("test.", ".fq.gz")
    outputR2B.deleteOnExit()

    DownsampleRegions.main(Array(
      "--bamFile", resourcePath("/wgs1.bam"),
      "--bedFile", resourcePath("/regions.bed"),
      "--inputR1", resourcePath("/R1.fq.gz"),
      "--inputR2", resourcePath("/R2.fq.gz"),
      "--outputR1A", outputR1A.getAbsolutePath,
      "--outputR2A", outputR2A.getAbsolutePath,
      "--outputR1B", outputR1B.getAbsolutePath,
      "--outputR2B", outputR2B.getAbsolutePath
      ))

    val readerR1A = new FastqReader(outputR1A)
    val r1aSize = readerR1A.iterator().size

    val readerR2A = new FastqReader(outputR2A)
    val r2aSize = readerR2A.iterator().size

    r1aSize shouldBe r2aSize

    val readerR1B = new FastqReader(outputR1B)
    val r1bSize = readerR1B.iterator().size

    val readerR2B = new FastqReader(outputR2B)
    val r2bSize = readerR2B.iterator().size

    r1bSize shouldBe r2bSize
  }

  @Test
  def testBamNotExist(): Unit = {
    val notExist = File.createTempFile("test.", ".test")
    notExist.delete()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", notExist.getAbsolutePath,
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bam file does not exist: ${notExist.getAbsolutePath}"
  }

  @Test
  def testBedNotExist(): Unit = {
    val notExist = File.createTempFile("test.", ".test")
    notExist.delete()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", notExist.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bed file does not exist: ${notExist.getAbsolutePath}"
  }

  @Test
  def testInputR1NotExist(): Unit = {
    val notExist = File.createTempFile("test.", ".test")
    notExist.delete()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", notExist.getAbsolutePath,
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Input R1 file does not exist: ${notExist.getAbsolutePath}"
  }

  @Test
  def testInputR2NotExist(): Unit = {
    val notExist = File.createTempFile("test.", ".test")
    notExist.delete()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", notExist.getAbsolutePath,
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Input R2 file does not exist: ${notExist.getAbsolutePath}"
  }

  @Test
  def testBedEmpty(): Unit = {
    val notExist = File.createTempFile("test.", ".test")
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", notExist.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bed file is empty"
  }

  @Test
  def testNoInputR2Paired(): Unit = {
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bam contains paired reads but input R2 is not defined"
  }

  @Test
  def testNoOutputR2APaired(): Unit = {
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bam contains paired reads but output A R2 is not defined"
  }

  @Test
  def testNoOutputR2BPaired(): Unit = {
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", resourcePath("/regions.bed"),
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2A", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Bam contains paired reads but output B R2 is not defined"
  }

  @Test
  def testNoScores(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    val writer = new PrintWriter(bedFile)
    writer.println("chr1\t1\t2")
    writer.close()
    bedFile.deleteOnExit()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", bedFile.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2A", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Region does not have a score/fraction"
  }

  @Test
  def testHighScores(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    val writer = new PrintWriter(bedFile)
    writer.println("chr1\t1\t2\tname\t1.1")
    writer.close()
    bedFile.deleteOnExit()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", bedFile.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2A", "a",
        "--outputR2B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Region score/fraction should be between -1.0 and 1.0"
  }

  @Test
  def testLowScores(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    val writer = new PrintWriter(bedFile)
    writer.println("chr1\t1\t2\tname\t-1.1")
    writer.close()
    bedFile.deleteOnExit()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", bedFile.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2A", "a",
        "--outputR2B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Region score/fraction should be between -1.0 and 1.0"
  }

  @Test
  def testOverlapScores(): Unit = {
    val bedFile = File.createTempFile("test.", ".bed")
    val writer = new PrintWriter(bedFile)
    writer.println("chr1\t1\t10\tname\t-0.1")
    writer.println("chr1\t5\t15\tname\t-0.1")
    writer.close()
    bedFile.deleteOnExit()
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array(
        "--bamFile", resourcePath("/wgs1.bam"),
        "--bedFile", bedFile.getAbsolutePath,
        "--inputR1", resourcePath("/R1.fq.gz"),
        "--inputR2", resourcePath("/R2.fq.gz"),
        "--outputR1A", "a",
        "--outputR1B", "a",
        "--outputR2A", "a",
        "--outputR2B", "a"
      ))
    }.getMessage shouldBe s"requirement failed: Regions are overlapping, this is not allowed"
  }

}
