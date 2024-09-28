package net.hennabatch.dojinapi.testutils

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import net.hennabatch.dojinapi.common.utils.logger

object TestFlags {

    //DBアクセスを伴うテストはフラグをOnにしないと実行できない
    val disableDBAccess: (TestCase) -> Enabled = {
        logger.info(System.getProperty("runDBAccessTest"))
        if (System.getProperty("runDBAccessTest")?.toBoolean() == true){
            Enabled.enabled
        }else{
            Enabled.disabled("DBアクセスを伴うテストは'runDBAccessTest'をtrueにして実行してください。")
        }
    }
}