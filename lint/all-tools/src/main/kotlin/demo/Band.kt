// SPDX-License-Identifier: Apache-2.0
package demo

private const val SUCCESS_FLOOR = 200
private const val REDIRECT_FLOOR = 300
private const val CLIENT_ERROR_FLOOR = 400
private const val SERVER_ERROR_FLOOR = 500

/** The band an HTTP status code falls in, audited by detekt's default rule set. */
enum class Band(val floor: Int) {
    SUCCESS(SUCCESS_FLOOR),
    REDIRECT(REDIRECT_FLOOR),
    CLIENT_ERROR(CLIENT_ERROR_FLOOR),
    SERVER_ERROR(SERVER_ERROR_FLOOR);

    companion object {
        /** The band [status] falls in, or null below the first band. */
        fun of(status: Int): Band? = entries.lastOrNull { status >= it.floor }
    }
}
