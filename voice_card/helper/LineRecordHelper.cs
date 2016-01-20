using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using voice_card.entity;
using log4net;

namespace voice_card.helper
{
    class LineRecordHelper
    {

        private static ILog log = LogManager.GetLogger(typeof(LineRecordHelper));

        private static RecordSave saveHelper;

        //来电记录，参数为外线通道
        public static void ComingCall(LineInfo trunk,LineInfo inline)
        {
            try
            {
                if (saveHelper == null)
                {
                    loadRecordSave();
                }
                saveHelper.Save(trunk, inline);
            }
            catch (Exception e)
            {
                log.Debug("保存数据失败!");
            }

        }

        /**
         * 加载语音存储对象
         **/ 
        private static void loadRecordSave()
        {
            string execClass = XmlService.getProperty("RecordSave","classname");
            Type type = Type.GetType(execClass);
            saveHelper = (RecordSave)Activator.CreateInstance(type);
         }


      
 
    }
}
