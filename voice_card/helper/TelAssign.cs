using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using voice_card.entity;

/**
 *电话分配接口
 **/ 
namespace voice_card.helper
{
    interface TelAssign
    {

        int Assign(ObservableCollection<LineInfo> Lines);

    }
}
