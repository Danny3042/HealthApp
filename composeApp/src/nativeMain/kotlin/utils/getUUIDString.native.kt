package utils

import platform.Foundation.NSUUID.Companion.UUID

/**
 * Returns UUID string.
 */
actual fun getUUIDString(): String {
    return UUID().UUIDString
}