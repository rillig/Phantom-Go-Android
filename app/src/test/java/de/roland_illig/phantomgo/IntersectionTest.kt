package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions
import org.junit.Test

class IntersectionTest {
    @Test
    fun testToString() {
        Assertions.assertThat("${Intersection(0, 0)}").isEqualTo("A1")
        Assertions.assertThat("${Intersection(0, 18)}").isEqualTo("A19")
        Assertions.assertThat("${Intersection(18, 0)}").isEqualTo("S1")
        Assertions.assertThat("${Intersection(18, 18)}").isEqualTo("S19")
        Assertions.assertThat("${Intersection(25, 25)}").isEqualTo("Z26")
    }
}
