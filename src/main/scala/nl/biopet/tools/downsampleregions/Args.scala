package nl.biopet.tools.downsampleregions

import java.io.File

/** Args for [[DownsampleRegions]] */
case class Args(bamFile: File = null,
                bedFile: File = null,
                inputR1: File = null,
                inputR2: Option[File] = None,
                outputR1A: File = null,
                outputR2A: Option[File] = None,
                outputR1B: File = null,
                outputR2B: Option[File] = None,
                deviation: Double = 0.0,
                seed: Long = 0)
