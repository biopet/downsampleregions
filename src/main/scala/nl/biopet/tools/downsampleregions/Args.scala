package nl.biopet.tools.downsampleregions

import java.io.File

case class Args(bamFile: File = null,
                bedFile: File = null,
                intputR1: File = null,
                intputR2: Option[File] = None,
                outputR1: File = null,
                outputR2: Option[File] = None,
                deviation: Double = 0.0,
                seed: Long = 0)
