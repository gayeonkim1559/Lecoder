package com.lecoder.team9.lecoder;

/**
 * Created by Schwa on 2017-11-21.
 */

public class RecordListItem {
    String recordName;
    String recordDate;
    String recordDuration;
    String recordClass; // 강의녹음만 적용
    String tag;
    public RecordListItem(String recordName, String recordDate, String recordDuration) {
        this.recordName = recordName;
        this.recordDate = recordDate;
        this.recordDuration = recordDuration;
        this.recordClass="";
    }

    public RecordListItem(String tag,String recordName, String recordDate, String recordDuration, String recordClass) {
        this.tag=tag;
        this.recordName = recordName;
        this.recordDate = recordDate;
        this.recordDuration = recordDuration;
        this.recordClass = recordClass;
    }
}
