package nl.biopet.tools.downsampleregions

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(cmdName: String) extends AbstractOptParser[Args](cmdName) {
  opt[File]("bamFile")
    .abbr("b")
    .required()
    .action((x, c) => c.copy(bamFile = x))
  opt[File]("bedFile")
    .abbr("L")
    .required()
    .action((x, c) => c.copy(bedFile = x))
  opt[File]("inputR1")
    .required()
    .action((x, c) => c.copy(intputR1 = x))
  opt[File]("inputR2")
    .action((x, c) => c.copy(intputR2 = Some(x)))
  opt[File]("outputR1")
    .required()
    .action((x, c) => c.copy(outputR1 = x))
  opt[File]("outputR2")
    .action((x, c) => c.copy(outputR2 = Some(x)))
  opt[Long]("seed")
    .action((x, c) => c.copy(seed = x))
}
