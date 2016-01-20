using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Web;
using voice_card.entity;
using System.IO;
 

namespace voice_card.service
{
    [ServiceContract]
    interface WebServerInterface
    {
        [OperationContract, WebGet(ResponseFormat = WebMessageFormat.Json)]
        LineInfo GetLineInfo(int lineNum,string gonghao);


        [WebInvoke(BodyStyle = WebMessageBodyStyle.WrappedRequest, ResponseFormat = WebMessageFormat.Json)]
        [OperationContract]
        void StateChange(int lineNum, int state);

        [OperationContract, WebGet]
        Stream Play(string recordId);


        //add 20100419
        //通道接听
        [OperationContract, WebGet]
        string Receiver(int lineNum);

        //通道挂机
        [OperationContract, WebGet]
        string HandUp(int lineNum);

        [OperationContract, WebGet]
        string test();

        [OperationContract, WebGet]
        void ConfirmHandup(int lineNum);

        [OperationContract, WebGet]
        string CallPhone(int lineNum,string phone);

        [OperationContract, WebGet]
        void SetBusy(int lineNum);

        [OperationContract, WebGet(UriTemplate = "/clientaccesspolicy.xml")]
        Stream GetSilverlightPolicy();

       [OperationContract, WebGet(UriTemplate = "/crossdomain.xml")]
       Stream GetFlashPolicy();



    }
}
