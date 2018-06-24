package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RefereeResultTest {

    @Test
    fun testToString() {
        assertThat(RefereeResult.Ok(true, true, 7).toString()).isEqualTo("atari, selfAtari, captured 7")
        assertThat(RefereeResult.Ok(false, false, 0).toString()).isEqualTo("ok")
        assertThat(RefereeResult.Ko.toString()).isEqualTo("ko")
        assertThat(RefereeResult.OwnStone.toString()).isEqualTo("ownStone")
        assertThat(RefereeResult.OtherStone.toString()).isEqualTo("otherStone")
        assertThat(RefereeResult.Suicide.toString()).isEqualTo("suicide")
        assertThat(RefereeResult.Pass.toString()).isEqualTo("pass")
    }
}
