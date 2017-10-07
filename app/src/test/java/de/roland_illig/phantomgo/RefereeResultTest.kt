package de.roland_illig.phantomgo

import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as eq

class RefereeResultTest {

    @Test
    fun testToString() {
        assertThat(RefereeResult.ok(true, true, 7).toString(), eq("atari, selfAtari, captured 7"))
        assertThat(RefereeResult.ok(false, false, 0).toString(), eq("ok"))
        assertThat(RefereeResult.ko().toString(), eq("ko"))
        assertThat(RefereeResult.ownStone().toString(), eq("ownStone"))
        assertThat(RefereeResult.otherStone().toString(), eq("otherStone"))
        assertThat(RefereeResult.suicide().toString(), eq("suicide"))
        assertThat(RefereeResult.pass().toString(), eq("pass"))
    }
}
