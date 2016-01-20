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
   public class Program
    {
        private static ILog Log = LogManager.GetLogger(typeof(Program));
        public static String RESULT_FILE_NAME = @"result.dat";

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
                    CardConfig cc = new CardConfig() { Name = ccPair[1] };
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
            int BaudRate1 = int.Parse(Config.GetConfig("Baud1"));
            short Port1 = short.Parse(Config.GetConfig("Port1"));
            Log.Debug(String.Join(" ", args));

            GenericService service = new GenericService(ci, Port, BaudRate,Port1,BaudRate1);
            Object obj = null;
            switch(args[0])
            {
                case "ReadCard":
                    obj = service.ReadCard();
                    break;
                case "WriteGasCard":
                    obj = service.WriteGasCard(args[1], args[2], args[3], args[4], 
                        int.Parse(args[5]), int.Parse(args[6]), int.Parse(args[7]), short.Parse(args[8]),
                        int.Parse(args[9]), int.Parse(args[10]), int.Parse(args[11]), int.Parse(args[12]),
                        args[13], args[14], int.Parse(args[15]), int.Parse(args[16]), args[17], args[18]
                        );
                    break;
                case "WriteNewCard":
                    obj = service.WriteNewCard(args[1], args[2], short.Parse(args[3]), args[4], args[5], args[6],args[7],
                        int.Parse(args[8]), int.Parse(args[9]), int.Parse(args[10]), short.Parse(args[11]),
                        int.Parse(args[12]), short.Parse(args[13]), int.Parse(args[14]), int.Parse(args[15]),
                        int.Parse(args[16]), int.Parse(args[17]), args[18], args[19], int.Parse(args[20]), int.Parse(args[21]), args[22], args[23]
                        );
                    break;
                case "FormatGasCard":
                    obj = service.FormatGasCard(args[1], args[2], args[3], args[4]);
                    break;
                default:
                    return;
            }

            String result = JsonConvert.SerializeObject(obj);
            //Console.Write(result);
            //File.WriteAllText(RESULT_FILE_NAME, result);
            File.WriteAllText("result.dat", result);
          
            Log.Debug(result);
            return;
        }
    }
}
