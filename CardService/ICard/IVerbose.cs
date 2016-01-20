using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ICard
{
    /// <summary>
    /// 实现此接口，可以得到和表厂相关的错误信息
    /// </summary>
    public interface IVerbose
    {
        String GetError(int errCode);
    }
}
