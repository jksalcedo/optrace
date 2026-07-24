package com.jksalcedo.optrace.core.parser

object Regexes {
    // "Uid 10234 package com.foo.bar:" or "Uid 10234 package com.foo.bar"
    val pkgWithUid = Regex("""\bUid\s+(\d+)\s+package\s+([A-Za-z0-9._$-]+)""", RegexOption.IGNORE_CASE)

    // "Package com.foo.bar:" or "package com.foo.bar:"
    val pkgOnly = Regex("""\bPackage\s+([A-Za-z0-9._$-]+)""", RegexOption.IGNORE_CASE)

    // "Uid 10234:"
    val uidOnly = Regex("""^\s*Uid\s+(\d+)\s*:?""", RegexOption.IGNORE_CASE)

    // Op line: "CAMERA: mode=allow" or "OP_CAMERA: mode=0" or "COARSE_LOCATION: mode=foreground"
    val opLine = Regex("""^\s*(?:OP_)?([A-Z0-9_:.()]+):\s*mode\s*=\s*([a-zA-Z0-9_]+)""", RegexOption.IGNORE_CASE)

    // Last access / reject
    val lastAccess = Regex("""\b(lastAccess(?:Time)?|time)\s*=\s*([^\s;]+)""", RegexOption.IGNORE_CASE)
    val lastReject = Regex("""\b(lastReject(?:Time)?)\s*=\s*([^\s;]+)""", RegexOption.IGNORE_CASE)

    val attribution = Regex("""\battribution(?:Tag)?\s*=\s*([A-Za-z0-9._:/$-]+)""", RegexOption.IGNORE_CASE)
}