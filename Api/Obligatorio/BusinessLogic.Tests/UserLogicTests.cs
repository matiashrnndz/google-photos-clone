using BusinessLogic.Interface;
using DataAccess;
using DataAccess.Interface;
using Domain;
using GoogleAPIInterface;
using Microsoft.EntityFrameworkCore;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System;
using System.Collections.Generic;
using System.Text;

namespace BusinessLogic.Tests
{
    [TestClass]
    public class UserLogicTests
    {
        private IUserLogic UserLogic;
        private ISessionLogic SessionLogic;
        private IAlbumLogic AlbumLogic;
        private DbContext context;
        Mock<IGoogleServices> GoogleServices;

        [TestInitialize]
        public void Initialize()
        {
            //context = InitializeSQL();
            context = InitializeMemory();

            IRepository<User> repository = new UserRepository(context);
            IRepository<Photo> photos = new PhotoRepository(context);
            IRepository<Album> albums = new AlbumRepository(context);
            GoogleServices = new Mock<IGoogleServices>(MockBehavior.Strict);
            UserLogic = new UserLogic(repository, GoogleServices.Object);
            SessionLogic = new SessionLogic(repository);
            AlbumLogic = new AlbumLogic(repository, photos, albums);
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

        public Photo Photo()
        {
            Photo photo = new Photo()
            {
                Type = "image/jpg",
                Latitude = "-34.903763",
                Longitude = "-56.190589",
                Location = "Montevideo, Uruguay"
            };
            return photo;
        }

        [TestMethod]
        public void Update()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = "newpassword456",
                PasswordConfirmation = "newpassword456",
                Name = "Paul",
                BornDate = Convert.ToDateTime("1995, 8, 13"),
                Phone = "094376153"
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
            Assert.IsTrue(updatedUser.Name.Equals("Paul") &&
                updatedUser.BornDate.Equals(Convert.ToDateTime("1995, 8, 13")) &&
                updatedUser.Phone.Equals("094376153"));
        }

        [ExpectedException(typeof(InvalidPasswordConfirmation))]
        [TestMethod]
        public void UpdateWithInvalidPasswordConfirmation()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = "newpassword456",
                PasswordConfirmation = "newpassword789",
                Name = User().Name,
                BornDate = User().BornDate,
                Phone = User().Phone
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [ExpectedException(typeof(InvalidPasswordFormat))]
        [TestMethod]
        public void UpdateWithInvalidShortPassword()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = "pass",
                PasswordConfirmation = "pass",
                Name = User().Name,
                BornDate = User().BornDate,
                Phone = User().Phone
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [ExpectedException(typeof(InvalidPasswordFormat))]
        [TestMethod]
        public void UpdateWithInvalidPasswordWithoutNumbers()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = "password",
                PasswordConfirmation = "password",
                Name = User().Name,
                BornDate = User().BornDate,
                Phone = User().Phone
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [ExpectedException(typeof(InvalidName))]
        [TestMethod]
        public void UpdateWithInvalidName()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = User().Password,
                PasswordConfirmation = User().PasswordConfirmation,
                Name = "",
                BornDate = User().BornDate,
                Phone = User().Phone
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [ExpectedException(typeof(InvalidDate))]
        [TestMethod]
        public void UpdateWithInvalidDate()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = User().Password,
                PasswordConfirmation = User().PasswordConfirmation,
                Name = User().Name,
                BornDate = Convert.ToDateTime("2999, 8, 13"),
                Phone = User().Phone
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [ExpectedException(typeof(InvalidPhone))]
        [TestMethod]
        public void UpdateWithInvalidPhone()
        {
            User user = SessionLogic.SignUp(User());
            User modifiedUser = new User()
            {
                Password = User().Password,
                PasswordConfirmation = User().PasswordConfirmation,
                Name = User().Name,
                BornDate = User().BornDate,
                Phone = "abc"
            };
            User updatedUser = UserLogic.Update(user.Email, modifiedUser);
        }

        [TestMethod]
        public void UploadPhoto()
        {
            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(new List<string>());

            User user = SessionLogic.SignUp(User());
            Photo photo = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImageInBase64>");
            Assert.IsTrue(photo.Latitude == Photo().Latitude &&
                photo.Longitude == Photo().Longitude &&
                photo.Type == Photo().Type &&
                photo.Location == Photo().Location);
        }

        [TestMethod]
        public void GetAllPhotos()
        {
            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(new List<string>());
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(new List<string>())
                .Returns(new List<string>());

            User user = SessionLogic.SignUp(User());
            Photo photo1 = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImage1InBase64>");
            Photo photo2 = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImage2InBase64>");
            Assert.AreEqual(UserLogic.GetAllPhotos(user.Email).Count, 2);
        }

        [TestMethod]
        public void GetAllAlbums()
        {
            List<string> tags = new List<string>();
            tags.Add("Cat");
            tags.Add("Feline");

            List<Tag> tagsList = new List<Tag>();
            tagsList.Add(Tag.ByName("Cat"));
            tagsList.Add(Tag.ByName("Feline"));

            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(tags);

            User user = SessionLogic.SignUp(User());
            Photo photo = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImageInBase64>");

            AlbumLogic.CreateAlbumByTags(User().Email, "Cats", tagsList);

            Assert.AreEqual(UserLogic.GetAllAlbums(user.Email).Count, 1);
        }
    }
}