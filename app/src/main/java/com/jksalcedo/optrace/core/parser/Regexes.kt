package com.jksalcedo.optrace.core.parser

object Regexes {
    // Examples matched:
    // "Uid 10234 package com.foo.bar:"
    // "package com.foo.bar:"
    val pkgWithUid = Regex("""\bUid\s+(\d+)\s+package\s+([A-Za-z0-9._$]+)\s*:?""")
    val pkgOnly = Regex("""\bpackage\s+([A-Za-z0-9._$]+)\s*:?""")

    // Examples matched:
    // "OP_CAMERA: mode=allow; ..."
    // "android:camera mode=foreground ..."
    val opLine = Regex("""\b([A-Z0-9_:.]+)\b.*\bmode\s*=\s*([a-zA-Z_]+)""")

    // Optional timing fields often seen in variants
    val lastAccess = Regex("""\b(lastAccess(?:Time)?|time)\s*=\s*([0-9]+)""")
    val lastReject = Regex("""\b(lastReject(?:Time)?)\s*=\s*([0-9]+)""")

    // Attribution-ish fragments
    val attribution = Regex("""\battribution(?:Tag)?\s*=\s*([A-Za-z0-9._:/$-]+)""")
}