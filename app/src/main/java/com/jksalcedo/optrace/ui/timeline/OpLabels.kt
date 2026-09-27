package com.jksalcedo.optrace.ui.timeline

/**
 * Resolves a raw AppOps op name to a human-readable label.
 *
 * The explicit map provides fine-grained labels (e.g. "Fine Location" vs "Coarse Location")
 * where the distinction matters. For everything else, we fall back to the [PermissionGroup]
 * group label rather than blindly tokenising the raw op string.
 */
object OpLabels {

    private val map = mapOf(
        // Camera
        "android:camera"                      to "Camera",

        // Location — distinguish precision
        "android:fine_location"               to "Precise Location",
        "android:coarse_location"             to "Approximate Location",
        "android:mock_location"               to "Mock Location",

        // Microphone
        "android:record_audio"                to "Microphone",

        // Contacts
        "android:read_contacts"               to "Read Contacts",
        "android:write_contacts"              to "Write Contacts",
        "android:get_accounts"                to "Account Access",

        // Storage / Media
        "android:read_external_storage"       to "Read Storage",
        "android:write_external_storage"      to "Write Storage",
        "android:manage_external_storage"     to "Manage Storage",
        "android:read_media_images"           to "Read Photos",
        "android:read_media_video"            to "Read Videos",
        "android:read_media_audio"            to "Read Audio",

        // Phone
        "android:read_phone_state"            to "Phone State",
        "android:read_phone_numbers"          to "Phone Numbers",
        "android:call_phone"                  to "Make Calls",
        "android:read_call_log"               to "Read Call Log",
        "android:write_call_log"              to "Write Call Log",
        "android:answer_phone_calls"          to "Answer Calls",
        "android:process_outgoing_calls"      to "Outgoing Calls",

        // Calendar
        "android:read_calendar"               to "Read Calendar",
        "android:write_calendar"              to "Write Calendar",

        // SMS
        "android:read_sms"                    to "Read SMS",
        "android:send_sms"                    to "Send SMS",
        "android:receive_sms"                 to "Receive SMS",
        "android:receive_mms"                 to "Receive MMS",

        // Sensors
        "android:body_sensors"                to "Body Sensors",
        "android:high_sampling_rate_sensors"  to "High-Rate Sensors",

        // Bluetooth / Nearby Devices
        "android:bluetooth_scan"              to "Bluetooth Scan",
        "android:bluetooth_connect"           to "Bluetooth Connect",
        "android:bluetooth_advertise"         to "Bluetooth Advertise",

        // Nearby WiFi
        "android:nearby_wifi_devices"         to "Nearby WiFi Devices",

        // Notifications
        "android:post_notification"           to "Post Notifications",

        // Misc system
        "android:get_usage_stats"             to "Usage Stats",
        "android:system_alert_window"         to "Draw over Other Apps",
        "android:write_settings"              to "Modify System Settings",
        "android:request_install_packages"    to "Install Unknown Apps",
        "android:picture_in_picture"          to "Picture-in-Picture",
        "android:read_clipboard"              to "Read Clipboard",
    )

    /**
     * Returns a readable label for [opName]. Falls back to the [PermissionGroup] group label
     * (e.g. "Storage") rather than exposing raw tokenised op strings.
     */
    fun label(opName: String): String =
        map[opName.lowercase().trim()]
            ?: PermissionGroup.of(opName).label
}
