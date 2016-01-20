using System;
using System.Net;

namespace Com.Aote.Logs
{
    // 信息输出器
    public interface IAppender
    {
        void ShowMessage(string msg);
    }
}
