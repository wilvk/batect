package batect.testutils

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import batect.config.io.ConfigurationException

fun withMessage(message: String): Matcher<Throwable> {
    return has(Throwable::message, equalTo(message))
}

fun withLineNumber(lineNumber: Int): Matcher<ConfigurationException> {
    return has(ConfigurationException::lineNumber, equalTo(lineNumber))
}