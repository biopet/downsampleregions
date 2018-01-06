package nl.biopet.tools.downsampleregions

import java.io.File

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

/** Argparser for [[DownsampleRegions]] */
class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]("bamFile")
    .abbr("b")
    .required()
    .action((x, c) => c.copy(bamFile = x))
    .text("Input bam file")
  opt[File]("bedFile")
    .abbr("L")
    .required()
    .action((x, c) => c.copy(bedFile = x))
    .text("Input bed file. 4e column defined the fraction")
  opt[File]("inputR1")
    .required()
    .action((x, c) => c.copy(inputR1 = x))
    .text("Input R1 fastq")
  opt[File]("inputR2")
    .action((x, c) => c.copy(inputR2 = Some(x)))
    .text("Input R2 fastq")
  opt[File]("outputR1A")
    .required()
    .action((x, c) => c.copy(outputR1A = x))
    .text("Output R1 fastq")
  opt[File]("outputR2A")
    .action((x, c) => c.copy(outputR2A = Some(x)))
    .text("Output R2 fastq")
  opt[File]("outputR1B")
    .required()
    .action((x, c) => c.copy(outputR1B = x))
    .text("Output R1 fastq")
  opt[File]("outputR2B")
    .action((x, c) => c.copy(outputR2B = Some(x)))
    .text("Output R2 fastq")
  opt[Double]("deviation")
    .action((x, c) => c.copy(deviation = x))
    .text("Deviation of fractions")
  opt[Long]("seed")
    .action((x, c) => c.copy(seed = x))
    .text("Random seed")
}
