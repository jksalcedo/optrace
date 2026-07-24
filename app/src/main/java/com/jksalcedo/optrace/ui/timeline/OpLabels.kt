package com.jksalcedo.optrace.ui.timeline

object OpLabels {
    private val map = mapOf(
        "android:camera" to "Camera",
        "android:fine_location" to "Fine Location",
        "android:coarse_location" to "Coarse Location",
        "android:record_audio" to "Microphone",
        "android:read_contacts" to "Read Contacts",
        "android:write_contacts" to "Write Contacts",
        "android:read_external_storage" to "Read Storage",
        "android:write_external_storage" to "Write Storage",
        "android:read_call_log" to "Read Call Log",
        "android:write_call_log" to "Write Call Log",
        "android:read_calendar" to "Read Calendar",
        "android:write_calendar" to "Write Calendar",
        "android:read_sms" to "Read SMS",
        "android:send_sms" to "Send SMS",
        "android:receive_sms" to "Receive SMS",
        "android:read_phone_state" to "Phone State",
        "android:body_sensors" to "Body Sensors",
        "android:get_usage_stats" to "Usage Stats",
        "android:system_alert_window" to "Overlay",
        "android:write_settings" to "Write Settings",
        "android:request_install_packages" to "Install Apps",
        "android:picture_in_picture" to "Picture in Picture",
        "android:read_clipboard" to "Clipboard",
        "android:nearby_wifi_devices" to "Nearby WiFi",
        "android:post_notification" to "Notifications",
        "android:bluetooth_scan" to "Bluetooth Scan",
        "android:bluetooth_connect" to "Bluetooth Connect",
        "android:bluetooth_advertise" to "Bluetooth Advertise"
    )

    fun label(opName: String): String =
        map[opName] ?: opName
            .removePrefix("android:")
            .replace('_', ' ')
            .replaceFirstChar { it.uppercase() }
}
