package com.jksalcedo.optrace.ui.timeline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class PermissionGroup(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
) {
    CAMERA(     "Camera",          Icons.Outlined.CameraAlt,      Color(0xFF1E88E5)),
    LOCATION(   "Location",        Icons.Outlined.LocationOn,     Color(0xFF43A047)),
    MICROPHONE( "Microphone",      Icons.Outlined.Mic,            Color(0xFFE53935)),
    CONTACTS(   "Contacts",        Icons.Outlined.Contacts,       Color(0xFF8E24AA)),
    STORAGE(    "Storage",         Icons.Outlined.FolderOpen,     Color(0xFFFB8C00)),
    PHONE(      "Phone",           Icons.Outlined.Phone,          Color(0xFF00ACC1)),
    CALENDAR(   "Calendar",        Icons.Outlined.CalendarMonth,  Color(0xFF3949AB)),
    SMS(        "SMS",             Icons.Outlined.Sms,            Color(0xFF6D4C41)),
    SENSORS(    "Body Sensors",    Icons.Outlined.MonitorHeart,   Color(0xFFD81B60)),
    BLUETOOTH(  "Nearby Devices",  Icons.Outlined.Bluetooth,      Color(0xFF039BE5)),
    NEARBY_WIFI("Nearby WiFi",     Icons.Outlined.Wifi,           Color(0xFF00897B)),
    NOTIFICATIONS("Notifications", Icons.Outlined.Notifications,  Color(0xFFF4511E)),
    OTHER(      "Other",           Icons.Outlined.Security,       Color(0xFF546E7A)),
    ;

    companion object {

        /** Resolves an op name (e.g. "android:camera") to the correct [PermissionGroup]. */
        fun of(opName: String): PermissionGroup {
            val key = opName.lowercase().trim()
            return opToGroup[key] ?: inferFromKeywords(key)
        }

        // Explicit mapping for well-known ops
        private val opToGroup: Map<String, PermissionGroup> = buildMap {
            // Camera
            put("android:camera",                     CAMERA)

            // Location
            put("android:fine_location",              LOCATION)
            put("android:coarse_location",            LOCATION)
            put("android:gps",                        LOCATION)
            put("android:monitor_location",           LOCATION)
            put("android:monitor_high_power_location",LOCATION)
            put("android:mock_location",              LOCATION)

            // Microphone
            put("android:record_audio",               MICROPHONE)

            // Contacts
            put("android:read_contacts",              CONTACTS)
            put("android:write_contacts",             CONTACTS)
            put("android:get_accounts",               CONTACTS)

            // Storage
            put("android:read_external_storage",      STORAGE)
            put("android:write_external_storage",     STORAGE)
            put("android:manage_external_storage",    STORAGE)
            put("android:read_media_images",          STORAGE)
            put("android:read_media_video",           STORAGE)
            put("android:read_media_audio",           STORAGE)

            // Phone
            put("android:read_phone_state",           PHONE)
            put("android:read_phone_numbers",         PHONE)
            put("android:call_phone",                 PHONE)
            put("android:read_call_log",              PHONE)
            put("android:write_call_log",             PHONE)
            put("android:add_voicemail",              PHONE)
            put("android:use_sip",                    PHONE)
            put("android:process_outgoing_calls",     PHONE)
            put("android:answer_phone_calls",         PHONE)

            // Calendar
            put("android:read_calendar",              CALENDAR)
            put("android:write_calendar",             CALENDAR)

            // SMS
            put("android:read_sms",                   SMS)
            put("android:write_sms",                  SMS)
            put("android:receive_sms",                SMS)
            put("android:receive_mms",                SMS)
            put("android:receive_wap_push",           SMS)
            put("android:send_sms",                   SMS)

            // Sensors
            put("android:body_sensors",               SENSORS)
            put("android:body_sensors_wrist_temperature", SENSORS)
            put("android:high_sampling_rate_sensors", SENSORS)

            // Bluetooth / Nearby Devices
            put("android:bluetooth_scan",             BLUETOOTH)
            put("android:bluetooth_connect",          BLUETOOTH)
            put("android:bluetooth_advertise",        BLUETOOTH)
            put("android:bluetooth_admin",            BLUETOOTH)
            put("android:bluetooth",                  BLUETOOTH)
            put("android:uwb_ranging",                BLUETOOTH)

            // Nearby WiFi
            put("android:nearby_wifi_devices",        NEARBY_WIFI)
            put("android:wifi_scan",                  NEARBY_WIFI)

            // Notifications
            put("android:post_notification",          NOTIFICATIONS)
        }

        /** Keyword-based fallback for ops not in the explicit map. */
        private fun inferFromKeywords(key: String): PermissionGroup = when {
            "camera"        in key -> CAMERA
            "location"      in key || "gps" in key -> LOCATION
            "audio"         in key || "record" in key || "microphone" in key -> MICROPHONE
            "contact"       in key -> CONTACTS
            "storage"       in key || "media" in key -> STORAGE
            "phone"         in key || "call" in key || "sip" in key -> PHONE
            "calendar"      in key -> CALENDAR
            "sms"           in key || "mms" in key || "wap" in key -> SMS
            "sensor"        in key -> SENSORS
            "bluetooth"     in key -> BLUETOOTH
            "wifi"          in key || "nearby" in key -> NEARBY_WIFI
            "notification"  in key -> NOTIFICATIONS
            else            -> OTHER
        }
    }
}
