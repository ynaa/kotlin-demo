package no.miles.kotlindemo

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.miles.kotlindemo.bankdb.common.endpoint_health_readiness
import org.slf4j.LoggerFactory

class DemoAppTest : StringSpec(){

    private val logger = LoggerFactory.getLogger(DemoAppTest::class.java)

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        GlobalScope.launch {
            val app = FrontApp()
            app.start()
        }
        Thread.sleep(1000L)
        logger.info("adapter ready")
    }

    private val client = HttpClient(CIO)

    init {
        "Getting data"{
            val response: HttpResponse = client.get("http://localhost:8080/$endpoint_health_readiness")
            response.status.value shouldBe 200
        }
    }
}
