using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace Card
{
    //明华动态库调用
    public static class MingHua
    {
        #region 明华动态库导入
        //初始化串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_init", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_init(Int16 com, Int32 baut);
        //关闭串口
        [DllImport("Mwic_32.dll", EntryPoint = "ic_exit", CallingConvention = CallingConvention.StdCall)]
        public static extern int ic_exit(int icdev);

        //测试卡状态
        [DllImport("Mwic_32.dll", EntryPoint = "get_status", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 get_status(int icdev, ref Int16 state);
        //检测卡
        [DllImport("Mwic_32.dll", EntryPoint = "chk_card", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 chk_card(int icdev);

        //测试24c02卡
        [DllImport("Mwic_32.dll", EntryPoint = "chk_24c02", CallingConvention = CallingConvention.StdCall)]
        public static extern int chk_24c02(int icdev);
        //读24c02卡
        [DllImport("Mwic_32.dll", EntryPoint = "srd_24c02", CallingConvention = CallingConvention.StdCall)]
        public static extern int srd_24c02(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //写24c02卡
        [DllImport("Mwic_32.dll", EntryPoint = "swr_24c02", CallingConvention = CallingConvention.StdCall)]
        public static extern int swr_24c02(int icdev, Int16 offset, Int16 len, byte[] data_buffer);

        //测试24c01a卡
        [DllImport("Mwic_32.dll", EntryPoint = "chk_24c01a", CallingConvention = CallingConvention.StdCall)]
        public static extern int chk_24c01a(int icdev);
        //读24c01a卡
        [DllImport("Mwic_32.dll", EntryPoint = "srd_24c01a", CallingConvention = CallingConvention.StdCall)]
        public static extern int srd_24c01a(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //写24c01a卡
        [DllImport("Mwic_32.dll", EntryPoint = "swr_24c01a", CallingConvention = CallingConvention.StdCall)]
        public static extern int swr_24c01a(int icdev, Int16 offset, Int16 len, byte[] data_buffer);

        //测试4442卡
        [DllImport("Mwic_32.dll", EntryPoint = "chk_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern int chk_4442(int icdev);
        //校验密码
        [DllImport("Mwic_32.dll", EntryPoint = "csc_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 csc_4442(int icdev, Int16 len, byte[] data_buffer);
        //读4442卡
        [DllImport("Mwic_32.dll", EntryPoint = "srd_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 srd_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //写4442卡
        [DllImport("Mwic_32.dll", EntryPoint = "swr_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 swr_4442(int icdev, Int16 offset, Int16 len, byte[] data_buffer);
        //读4442卡错误计数
        [DllImport("Mwic_32.dll", EntryPoint = "rsct_4442", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 rsct_4442(int icdev, ref Int16 counter);

        //检测102卡
        [DllImport("Mwic_32.dll", EntryPoint = "chk_102", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 chk_102(int icdev);
        //读102卡
        [DllImport("Mwic_32.dll", EntryPoint = "srd_102", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 srd_102(int icdev, Int16 zone, Int16 offset, Int16 len, byte[] data_buffer);

        //擦102卡
        [DllImport("Mwic_32.dll", EntryPoint = "ser_102", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 ser_102(int icdev, Int16 zone, Int16 offset, Int16 len);

        //写102卡
        [DllImport("Mwic_32.dll", EntryPoint = "swr_102", CallingConvention = CallingConvention.StdCall)]
        public static extern Int16 swr_102(int icdev, Int16 zone, Int16 offset, Int16 len, byte[] data_buffer);

        #endregion

        //清除新卡卡上内容，4442卡默认密码ffffff，只做了24c01a，24c02, 4442卡
        public static int ClearCard(short com, int baud)
        {
            //打开串口
            int icdev = ic_init(com, baud);
            if (icdev < 0)
            {
                return -1;
            }
            //清除卡上内容
            if (chk_24c02(icdev) == 0)
            {
                byte[] data_buffer = new byte[0x100];
                for (int i = 0; i < data_buffer.Length; i++)
                {
                    data_buffer[i] = 0xff;
                }
                swr_24c02(icdev, 0, 0x100, data_buffer);
            }
            else if (chk_24c01a(icdev) == 0)
            {
                byte[] data_buffer = new byte[0x80];
                for (int i = 0; i < data_buffer.Length; i++)
                {
                    data_buffer[i] = 0xff;
                }
                swr_24c01a(icdev, 0, 0x80, data_buffer);
            }
            else if (chk_4442(icdev) == 0)
            {
                byte[] passwd = new byte[3] { 0xff, 0xff, 0xff };
                csc_4442(icdev, 3, passwd);
                byte[] data_buffer = new byte[0xe0];
                for (int i = 0; i < data_buffer.Length; i++)
                {
                    data_buffer[i] = 0xff;
                }
                swr_4442(icdev, 0x20, 0xe0, data_buffer);
            }
            //关闭串口
            ic_exit(icdev);
            return 0;
        }

        //看是否4442卡
        public static int Is4442(short com, int baud)
        {
            //打开串口
            int icdev = ic_init(com, baud);
            if (icdev < 0)
            {
                return -1;
            }
            int ret = chk_4442(icdev);
            //关闭串口
            ic_exit(icdev);
            return ret;
        }

        //检测是否空白卡，包括设备各种状态检测，以及是否好卡检测
        public static int CheckCard(short com, int baud)
        {
            //打开串口
            int icdev = ic_init(com, baud);
            if (icdev < 0)
            {
                //返回打开串口错误
                return -2;
            }
            Int16 state = 0;
            Int16 ret = get_status(icdev, ref state);
            if (ret != 0)
            {
                ic_exit(icdev);
                //返回读设备状态失败
                return -3;
            }
            if (state == 0)
            {
                ic_exit(icdev);
                //返回无卡
                return -4;
            }
            Int16 type = chk_card(icdev);
            //卡插反
            if (type == 0)
            {
                ic_exit(icdev);
                //返回卡插反
                return -19;
            }
            //102卡错卡，及空白卡测试
            if (type == 51)
            {
                byte[] buffer = new byte[100];
                //看计数错误，0F，错卡
                ret = srd_102(icdev, 0, 0x0c, 1, buffer);
                if (ret != 0)
                {
                    ic_exit(icdev);
                    //返回读卡密码次数失败
                    return -5;
                }
                if (buffer[0] == 0x0F)
                {
                    ic_exit(icdev);
                    //返回错卡
                    return -6;
                }
                //看是否空卡
                ret = srd_102(icdev, 0, 0x02, 8, buffer);
                if (ret != 0)
                {
                    ic_exit(icdev);
                    //返回读卡错误
                    return -7;
                }
                if (!IsEmpty(buffer, 0, 8, 0xFF))
                {
                    ic_exit(icdev);
                    //返回该卡不是新卡
                    return -15;
                }
                ret = srd_102(icdev, 0, 0x0E, 8, buffer);
                if (ret != 0)
                {
                    ic_exit(icdev);
                    //返回读卡错误
                    return -7;
                }
                if (!IsEmpty(buffer, 0, 8, 0xFF))
                {
                    ic_exit(icdev);
                    //返回该卡不是新卡
                    return -15;
                }
                //返回新卡或卡插反
                ic_exit(icdev);
                return -13;
            }
            //4442卡错卡及空白卡判断
            else if (type == 21)
            {
                byte[] buffer = new byte[100];
                //看计数错误，0，错卡
                Int16 num = 0;
                ret = rsct_4442(icdev, ref num);
                if (ret != 0)
                {
                    ic_exit(icdev);
                    //返回读卡密码次数失败
                    return -5;
                }
                if (num == 0)
                {
                    ic_exit(icdev);
                    //返回错卡
                    return -6;
                }
                //看是否空卡，只读前两行，前两行为空，说明是空卡
                ret = srd_4442(icdev, 0x20, 0x20, buffer);
                if (ret != 0)
                {
                    ic_exit(icdev);
                    //返回读卡错误
                    return -7;
                }
                if (!IsEmpty(buffer, 0, 0x20, 0xFF))
                {
                    ic_exit(icdev);
                    //返回该卡不是新卡
                    return -15;
                }
                //返回新卡或卡插反
                ic_exit(icdev);
                return -13;
            }
            //关闭串口
            ic_exit(icdev);
            return 0;
        }

        //检测内存是否全部是某个内容
        public static bool IsEmpty(byte[] buf, int start, int len, byte b)
        {
            for (int i = start; i < start + len; i++)
            {
                if (buf[i] != b)
                {
                    return false;
                }
            }
            return true;
        }
    }
}
