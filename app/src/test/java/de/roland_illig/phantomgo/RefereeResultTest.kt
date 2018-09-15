package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RefereeResultTest {

    @Test
    fun testToString() {
        fun i(x: Int, y: Int) = Intersection(x, y)

        assertThat(RefereeResult.Ok(true, true, listOf(i(0, 0), i(0, 1), i(0, 2))).toString()).isEqualTo("atari, selfAtari, captured 3")
        assertThat(RefereeResult.Ok(false, false, listOf()).toString()).isEqualTo("ok")
        assertThat(RefereeResult.Ko.toString()).isEqualTo("ko")
        assertThat(RefereeResult.OwnStone.toString()).isEqualTo("ownStone")
        assertThat(RefereeResult.OtherStone.toString()).isEqualTo("otherStone")
        assertThat(RefereeResult.Suicide.toString()).isEqualTo("suicide")
        assertThat(RefereeResult.Pass.toString()).isEqualTo("pass")
    }
}
