using BusinessLogic.Interface;
using DataAccess.Interface;
using Domain;
using System;
using System.Linq;

namespace BusinessLogic
{
    public class SessionLogic : ISessionLogic
    {
        private IRepository<User> Users;

        public SessionLogic(IRepository<User> repository)
        {
            this.Users = repository;
        }

        public User SignIn(string email, string password)
        {
            CheckValidEmail(email);
            string encryptedPassword = User.EncryptSHA256(password);
            bool exists = Users.GetAll().Exists(u => u.Email.Equals(email) && u.Password.Equals(encryptedPassword));
            if (exists)
            {
                return Users.GetAll().Find(u => u.Email.Equals(email) && u.Password.Equals(encryptedPassword));
            }
            else
            {
                throw new InvalidCredentials();
            }
        }

        private void CheckValidEmail(string email)
        {
            try
            {
                new System.Net.Mail.MailAddress(email);
            }
            catch (Exception)
            {
                throw new InvalidEmail();
            }
        }

        public User SignUp(User user)
        {
            user.Validate();
            user.EncryptPassword();
            return Users.Add(user);
        }
    }
}
