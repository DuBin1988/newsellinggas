using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;

/**
 * 语音信息存储接口
 **/ 
namespace voice_card.helper
{
    interface RecordSave
    {

        void Save(LineInfo trunk, LineInfo inline);
    }
}
