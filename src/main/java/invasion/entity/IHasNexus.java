package invasion.entity;

import invasion.nexus.Nexus;

/**
 * Interface by objects that can be acquired by a Nexus and also return that nexus
 */
public interface IHasNexus {
    Nexus getNexus();

    void acquiredByNexus(Nexus nexus);
}