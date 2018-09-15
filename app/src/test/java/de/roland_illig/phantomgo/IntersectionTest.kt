package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions
import org.junit.Test

class IntersectionTest {
    @Test
    fun testToString() {
        Assertions.assertThat(Intersection(0, 0).toString()).isEqualTo("A1")
        Assertions.assertThat(Intersection(0, 18).toString()).isEqualTo("A19")
        Assertions.assertThat(Intersection(18, 0).toString()).isEqualTo("S1")
        Assertions.assertThat(Intersection(18, 18).toString()).isEqualTo("S19")
        Assertions.assertThat(Intersection(25, 25).toString()).isEqualTo("Z26")
    }
}
