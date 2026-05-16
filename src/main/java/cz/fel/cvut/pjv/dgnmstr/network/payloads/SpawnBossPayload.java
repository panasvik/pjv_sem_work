package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;

import java.io.Serializable;

public record SpawnBossPayload(int id,
                               double x,
                               double y,
                               BossStats stats)
        implements Serializable, IDLinked {

}
