package io.jenkins.plugins.github.release;

import java.io.Serializable;

class SymantecVersionReleaseComparator extends AbstractReleaseComparator implements Serializable {

    @Override
    protected int doCompare(Release o1, Release o2) {
        if (SymantecVersion.isSymantecVersion(o1.tagName) && !SymantecVersion.isSymantecVersion(o2.tagName)) {
            return 1;
        } else if (!SymantecVersion.isSymantecVersion(o1.tagName) && SymantecVersion.isSymantecVersion(o2.tagName)) {
            return -1;
        } else if (!SymantecVersion.isSymantecVersion(o1.tagName) && !SymantecVersion.isSymantecVersion(o2.tagName)) {
            return 0;
        }

        SymantecVersion v1 = SymantecVersion.of(o1.tagName);
        SymantecVersion v2 = SymantecVersion.of(o2.tagName);
        return v1.compareTo(v2);
    }
}
