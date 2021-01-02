using BusinessLogic.Interface;
using DataAccess;
using DataAccess.Interface;
using Domain;
using Domain.Exceptions;
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
    public class AlbumLogicTests
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
                Type = "image/jpg"
            };
            return photo;
        }

        [TestMethod]
        public void CreateAlbum()
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

            Album generatedAlbum = AlbumLogic.CreateAlbumByTags(User().Email, "Cats", tagsList);

            Assert.IsTrue(generatedAlbum.Photos().Contains(photo));
        }

        [ExpectedException(typeof(NoTags))]
        [TestMethod]
        public void CreateAlbumWithNoTags()
        {
            AlbumLogic.CreateAlbumByTags(User().Email, "Cats", new List<Tag>());
        }

        [ExpectedException(typeof(NoAlbumName))]
        [TestMethod]
        public void CreateAlbumWithNoName()
        {
            AlbumLogic.CreateAlbumByTags(User().Email, "", new List<Tag>());
        }

        [ExpectedException(typeof(ExistentAlbumName))]
        [TestMethod]
        public void CreateAlbumWithExistentName()
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

            Album generatedAlbum = AlbumLogic.CreateAlbumByTags(User().Email, "Cats", tagsList);
            AlbumLogic.CreateAlbumByTags(User().Email, "Cats", tagsList);
        }

        [TestMethod]
        public void DeleteAlbum()
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

            Album generatedAlbum = AlbumLogic.CreateAlbumByTags(User().Email, "Cats", tagsList);

            AlbumLogic.DeleteAlbum(generatedAlbum.Id);

            Assert.IsFalse(AlbumLogic.ExistsById(generatedAlbum.Id));
        }
    }
}
