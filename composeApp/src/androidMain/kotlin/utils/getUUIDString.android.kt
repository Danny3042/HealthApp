package utils

import java.util.UUID

/**
 * Returns UUID string.
 */
actual fun getUUIDString(): String {
   return UUID.randomUUID().toString()
}