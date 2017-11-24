package nl.biopet.tools.downsampleregions

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(cmdName: String) extends AbstractOptParser[Args](cmdName) {
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
    .action((x, c) => c.copy(intputR1 = x))
    .text("Input R1 fastq")
  opt[File]("inputR2")
    .action((x, c) => c.copy(intputR2 = Some(x)))
    .text("Input R2 fastq")
  opt[File]("outputR1")
    .required()
    .action((x, c) => c.copy(outputR1 = x))
    .text("Output R1 fastq")
  opt[File]("outputR2")
    .action((x, c) => c.copy(outputR2 = Some(x)))
    .text("Output R2 fastq")
  opt[Double]("deviation")
    .action((x, c) => c.copy(deviation = x))
    .text("Deviation of fractions")
  opt[Long]("seed")
    .action((x, c) => c.copy(seed = x))
    .text("Random seed")
}
