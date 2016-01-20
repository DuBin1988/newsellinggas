using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Diagnostics;
using System.Threading;

namespace Com.Aote.Behavior
{
    public class MouseClickManager
    {

        #region Private members

        private event MouseButtonEventHandler _click;

        private event MouseButtonEventHandler _doubleClick;

        #endregion

        #region Constructor

        /// <summary>

        /// Initializes a new instance of the <see cref="MouseClickManager"/> class.

        /// </summary>

        /// <param name="control">The control.</param>

        public MouseClickManager(int doubleClickTimeout)
        {

            this.Clicked = false;

            this.DoubleClickTimeout = doubleClickTimeout;

        }

        #endregion

        #region Events

        public event MouseButtonEventHandler Click
        {

            add { _click += value; }

            remove { _click -= value; }

        }

        public event MouseButtonEventHandler DoubleClick
        {

            add { _doubleClick += value; }

            remove { _doubleClick -= value; }

        }

        /// <summary>

        /// Called when [click].

        /// </summary>

        /// <param name="sender">The sender.</param>

        /// <param name="e">The <see cref="System.Windows.Input.MouseButtonEventArgs"/> instance containing the event data.</param>

        private void OnClick(object sender, MouseButtonEventArgs e)
        {

            if (_click != null)
            {

                Debug.Assert(sender is Control);

                (sender as Control).Dispatcher.BeginInvoke(_click, sender, e);

            }

        }

        /// <summary>

        /// Called when [double click].

        /// </summary>

        /// <param name="sender">The sender.</param>

        /// <param name="e">The <see cref="System.Windows.Input.MouseButtonEventArgs"/> instance containing the event data.</param>

        private void OnDoubleClick(object sender, MouseButtonEventArgs e)
        {

            if (_doubleClick != null)
            {

                _doubleClick(sender, e);

            }

        }

        /// <summary>

        /// Handles the click.

        /// </summary>

        /// <param name="sender">The sender.</param>

        /// <param name="e">The <see cref="System.Windows.Input.MouseButtonEventArgs"/> instance containing the event data.</param>

        public void HandleClick(object sender, MouseButtonEventArgs e)
        {

            lock (this)
            {

                if (this.Clicked)
                {

                    this.Clicked = false;

                    OnDoubleClick(sender, e);

                }

                else
                {

                    this.Clicked = true;

                    ParameterizedThreadStart threadStart = new ParameterizedThreadStart(ResetThread);

                    Thread thread = new Thread(threadStart);

                    thread.Start(e);

                }

            }

        }

        #endregion

        #region Properties

        /// <summary>

        /// Gets or sets a value indicating whether this <see cref="MouseClickManager"/> is clicked.

        /// </summary>

        /// <value><c>true</c> if clicked; otherwise, <c>false</c>.</value>

        private bool Clicked { get; set; }

        /// <summary>

        /// Gets or sets the timeout.

        /// </summary>

        /// <value>The timeout.</value>

        public int DoubleClickTimeout { get; set; }

        #endregion

        #region Methods

        #region Private

        /// <summary>

        /// Resets the thread.

        /// </summary>

        /// <param name="state">The state.</param>

        private void ResetThread(object state)
        {

            Thread.Sleep(this.DoubleClickTimeout);



            lock (this)
            {

                if (this.Clicked)
                {

                    this.Clicked = false;

                    OnClick(this, (MouseButtonEventArgs)state);

                }

            }

        }

        #endregion

        #endregion


    }

}
