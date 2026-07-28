package com.jksalcedo.optrace.core.parser

object Regexes {
    // "Uid 10234 package com.foo.bar:" or "Uid 10234 package com.foo.bar"
    val pkgWithUid = Regex("""\bUid\s+(\d+)\s+package\s+([A-Za-z0-9._$-]+)""", RegexOption.IGNORE_CASE)

    // "Package com.foo.bar:" or "package com.foo.bar:" or "Package: com.foo.bar"
    val pkgOnly = Regex("""\bPackage\s*[:=]?\s*([A-Za-z0-9._$-]+)""", RegexOption.IGNORE_CASE)

    // "Uid 10234:"
    val uidOnly = Regex("""^\s*Uid\s+(\d+)\s*:?""", RegexOption.IGNORE_CASE)

    // Op line variants:
    // "CAMERA: mode=allow"
    // "Op 26 (CAMERA): mode=allow"
    // "OP_CAMERA: mode=0"
    // "android:camera: mode=allow"
    val opLineWithMode = Regex("""(?:Op\s+\d+\s*\()?([A-Z0-9_:.()-]+)\)?\s*[:=]\s*mode\s*=\s*([a-zA-Z0-9_]+)""", RegexOption.IGNORE_CASE)

    // Alternative op line format: "CAMERA: allow" or "CAMERA (allow)"
    val opLineSimple = Regex("""^\s*(?:OP_)?([A-Z0-9_]{3,})\s*[:=]\s*([a-zA-Z0-9_]+)""", RegexOption.IGNORE_CASE)

    // Last access / reject timing fields
    val lastAccess = Regex("""\b(lastAccess(?:Time)?|time)\s*=\s*([^\s;]+)""", RegexOption.IGNORE_CASE)
    val lastReject = Regex("""\b(lastReject(?:Time)?)\s*=\s*([^\s;]+)""", RegexOption.IGNORE_CASE)

    // Attribution tag
    val attribution = Regex("""\battribution(?:Tag)?\s*=\s*([A-Za-z0-9._:/$-]+)""", RegexOption.IGNORE_CASE)
}