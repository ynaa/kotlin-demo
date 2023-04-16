package no.miles.kotlindemo

import io.kotest.assertions.fail
import io.kotest.assertions.retry
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
import kotlin.time.Duration.Companion.seconds

class FrontAppTest : StringSpec(){

    private val logger = LoggerFactory.getLogger(FrontAppTest::class.java)

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        GlobalScope.launch {
            val app = FrontApp()
            app.start()
        }
        logger.info("adapter started ready")
    }

    private val client = HttpClient(CIO)

    init {

        "App test"{
            val response: HttpResponse = retry(maxRetry = 3, timeout = 3.seconds) {
                try {
                    val response = client.get("http://localhost:8080/$endpoint_health_readiness")
                    if (response.status.value != 200) {
                        fail("Component not ready")
                    }
                    response
                }
                catch (ex: Exception){
                    fail(ex.message ?: "Unable to connect to component")
                }
            }
            response.status.value shouldBe 200
        }
    }
}
