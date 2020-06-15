package invasion.entity;

import invasion.INotifyTask;


public interface ITerrainModify {
    boolean isReadyForTask(INotifyTask paramINotifyTask);

    boolean requestTask(ModifyBlockEntry[] paramArrayOfModifyBlockEntry, INotifyTask paramINotifyTask1, INotifyTask paramINotifyTask2);

    ModifyBlockEntry getLastBlockModified();
}