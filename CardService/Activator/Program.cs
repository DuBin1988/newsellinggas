using Card;
using Com.Aote.Logs;
using log4net;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace Card
{
    class Program
    {
        private static ILog Log = LogManager.GetLogger(typeof(Program));

        static void Main(string[] args)
        {
            CardInfos ci = new CardInfos();
            try
            {
                StreamReader sr = new StreamReader("card.config");
                string line;
                while ((line = sr.ReadLine()) != null)
                {
                    string[] attr = line.Split(',');
                    String[] ccPair = attr[0].Split('=');
                    String[] icard = attr[1].Split('=');
                    CardConfig cc = new CardConfig() { Name = ccPair[1].Trim() };
                    ICard card = (ICard)Assembly.GetExecutingAssembly().CreateInstance(icard[1].Trim());
                    cc.Card = card;
                    ci.Add(cc);
                }
                sr.Close();
            }
            catch(Exception e)
            {
                String config = JsonConvert.SerializeObject(new Ret() {  Err="卡配置文件错误。"});
                Console.Write(config);
                Log.Debug(config);
                return;                
            }

            int BaudRate = int.Parse(Config.GetConfig("Baud"));
            short Port = short.Parse(Config.GetConfig("Port"));
            Log.Debug(String.Join(" ", args));
            try
            {
                GenericService service = new GenericService(ci, Port, BaudRate);
                Object obj = null;
                switch (args[0])
                {
                    case "ReadCard":
                        obj = service.ReadCard();
                        break;
                    case "WriteGasCard":
                        byte[] array = Encoding.ASCII.GetBytes(args[1]);            
                        MemoryStream stream = new MemoryStream(array);
                        obj = service.WriteGasCard(stream, args[2], args[3], args[4],
                            args[5], args[6], args[7], args[8],
                            args[9], args[10], args[11], args[12],
                            args[13], args[14], args[15], args[16], args[17], args[18], args[19]
                            );
                        break;
                    case "WriteNewCard":
                        byte[] array1 = Encoding.ASCII.GetBytes(args[1]);            
                        MemoryStream stream1 = new MemoryStream(array1);
                        obj = service.WriteNewCard(stream1, args[2], args[3], args[4], args[5], args[6], args[7],
                            args[8], args[9], args[10], args[11],
                            args[12], args[13], args[14], args[15],
                            args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23]
                            , args[24], args[25], args[26]);
                        break;
                    case "FormatGasCard":
                        obj = service.FormatGasCard(args[1], args[2], args[3], args[4]);
                        break;
                    case "OpenCard":
                        obj = service.OpenCard(args[1], args[2], args[3], args[4]);
                        break;
                    default:
                        return;
                }

                String result = JsonConvert.SerializeObject(obj);
                File.WriteAllText("result.dat", result);
                Log.Debug(result);
            }
            catch (Exception ee)
            {
                Log.Debug("Activter异常："+ee.Message+"-----"+ee.StackTrace);
            }
           
            return;
        }
    }
}
