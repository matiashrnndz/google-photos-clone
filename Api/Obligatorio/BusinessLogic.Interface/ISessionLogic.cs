using Domain;
using System;

namespace BusinessLogic.Interface
{
    public interface ISessionLogic
    {
        User SignUp(User user);
        User SignIn(string email, string password);
    }
}
