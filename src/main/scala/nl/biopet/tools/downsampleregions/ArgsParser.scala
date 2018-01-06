/*
 * Copyright (c) 2017 Sequencing Analysis Support Core - Leiden University Medical Center
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
