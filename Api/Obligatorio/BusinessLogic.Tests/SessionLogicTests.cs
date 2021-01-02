using BusinessLogic.Interface;
using DataAccess;
using DataAccess.Interface;
using Domain;
using GoogleAPIInterface;
using Microsoft.EntityFrameworkCore;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System;

namespace BusinessLogic.Tests
{
    [TestClass]
    public class SessionLogicTests
    {
        private ISessionLogic SessionLogic;
        private IUserLogic UserLogic;
        private DbContext context;

        [TestInitialize]
        public void Initialize()
        {
            //context = InitializeSQL();
            context = InitializeMemory();

            IRepository<User> repository = new UserRepository(context);
            Mock<IGoogleServices> googleServices = new Mock<IGoogleServices>(MockBehavior.Strict);
            UserLogic = new UserLogic(repository, googleServices.Object);
            SessionLogic = new SessionLogic(repository);
        }

        private DbContext InitializeSQL()
        {

            context = ContextFactory.GetNewContextForTesting(ContextType.SQL);
            context.Database.EnsureDeleted();
            context.Database.EnsureCreated();
            return context;
        }

        private DbContext InitializeMemory()
        {
            context = ContextFactory.GetNewContextForTesting(ContextType.MEMORY);
            return context;
        }

        [TestCleanup]
        public void Cleanup()
        {
            context.Database.EnsureDeleted();
        }

        public User User(string email = "user@example.com",
            string password = "password123",
            string passwordConfirmation = "password123",
            string name = "User",
            string bornDate = "1996, 9, 24",
            string phone = "093333333")
        {
            User user = new User()
            {
                Email = email,
                Password = password,
                PasswordConfirmation = passwordConfirmation,
                Name = name,
                BornDate = Convert.ToDateTime(bornDate),
                Phone = phone
            };
            return user;
        }

        [TestMethod]
        public void SignUp()
        {
            User user = SessionLogic.SignUp(User());
            Assert.IsTrue(UserLogic.Exists(user.Id));
        }

        [ExpectedException(typeof(InvalidEmail))]
        [TestMethod]
        public void SignUpWithInvalidEmail()
        {
            User user = SessionLogic.SignUp(User(email: "wrongemail"));
        }

        [ExpectedException(typeof(InvalidPasswordConfirmation))]
        [TestMethod]
        public void SignUpWithInvalidPasswordConfirmation()
        {
            User user = SessionLogic.SignUp(User(passwordConfirmation: "wrongpassword"));
        }

        [ExpectedException(typeof(InvalidPasswordFormat))]
        [TestMethod]
        public void SignUpWithInvalidShortPassword()
        {
            User user = SessionLogic.SignUp(User(password: "pass", passwordConfirmation: "pass"));
        }

        [ExpectedException(typeof(InvalidPasswordFormat))]
        [TestMethod]
        public void SignUpWithInvalidPasswordWithoutNumbers()
        {
            User user = SessionLogic.SignUp(User(password: "password", passwordConfirmation: "password"));
        }

        [ExpectedException(typeof(InvalidName))]
        [TestMethod]
        public void SignUpWithInvalidName()
        {
            User user = SessionLogic.SignUp(User(name: ""));
        }

        [ExpectedException(typeof(InvalidDate))]
        [TestMethod]
        public void SignUpWithInvalidDate()
        {
            User user = SessionLogic.SignUp(User(bornDate: "2999, 9, 24"));
        }

        [ExpectedException(typeof(InvalidPhone))]
        [TestMethod]
        public void SignUpWithInvalidPhone()
        {
            User user = SessionLogic.SignUp(User(phone: "abc"));
        }

        [TestMethod]
        public void SignIn()
        {
            SessionLogic.SignUp(User());
            User user = SessionLogic.SignIn(User().Email, User().Password);
            Assert.AreEqual(user.Email, User().Email);
        }

        [ExpectedException(typeof(InvalidEmail))]
        [TestMethod]
        public void SignInWithInvalidEmail()
        {
            User user = SessionLogic.SignIn("wrongemail", User().Password);
        }

        [ExpectedException(typeof(InvalidCredentials))]
        [TestMethod]
        public void SignInWithInvalidCredentials()
        {
            User user = SessionLogic.SignIn("unexistent@email.com", "unexistentpassword");
        }
    }
}
