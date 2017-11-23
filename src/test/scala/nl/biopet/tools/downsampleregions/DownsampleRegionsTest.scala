package nl.biopet.tools.downsampleregions

import nl.biopet.test.BiopetTest
import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

class DownsampleRegionsTest extends ToolTest[Args] {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      DownsampleRegions.main(Array())
    }
  }

  def toolCommand: DownsampleRegions.type = DownsampleRegions
}
