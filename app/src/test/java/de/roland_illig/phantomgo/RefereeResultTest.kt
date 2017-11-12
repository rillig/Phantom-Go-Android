package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RefereeResultTest {

    @Test
    fun testToString() {
        assertThat(RefereeResult.ok(true, true, 7).toString()).isEqualTo("atari, selfAtari, captured 7")
        assertThat(RefereeResult.ok(false, false, 0).toString()).isEqualTo("ok")
        assertThat(RefereeResult.ko().toString()).isEqualTo("ko")
        assertThat(RefereeResult.ownStone().toString()).isEqualTo("ownStone")
        assertThat(RefereeResult.otherStone().toString()).isEqualTo("otherStone")
        assertThat(RefereeResult.suicide().toString()).isEqualTo("suicide")
        assertThat(RefereeResult.pass().toString()).isEqualTo("pass")
    }
}
