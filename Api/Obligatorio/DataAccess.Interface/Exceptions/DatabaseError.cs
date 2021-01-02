using System;
using System.Collections.Generic;
using System.Text;

namespace DataAccess.Interface.Exceptions
{
    public class DatabaseError : Exception
    {
        public DatabaseError(string message) : base(message) { }
        public DatabaseError(string message, Exception ex) : base(message, ex) { }
    }
}
