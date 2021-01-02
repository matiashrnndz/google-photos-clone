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
    public class PhotoLogicTests
    {
        private IUserLogic UserLogic;
        private ISessionLogic SessionLogic;
        private IPhotoLogic PhotoLogic;
        private DbContext context;
        Mock<IGoogleServices> GoogleServices;

        [TestInitialize]
        public void Initialize()
        {
            //context = InitializeSQL();
            context = InitializeMemory();

            IRepository<User> repository = new UserRepository(context);
            IRepository<Photo> photos = new PhotoRepository(context);
            GoogleServices = new Mock<IGoogleServices>(MockBehavior.Strict);
            UserLogic = new UserLogic(repository, GoogleServices.Object);
            SessionLogic = new SessionLogic(repository);
            PhotoLogic = new PhotoLogic(repository, photos, GoogleServices.Object);
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
        public void SearchByTags()
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
            List<Photo> returnedPhotos = PhotoLogic.SearchByTags(user.Email, tagsList);
            Assert.AreEqual(returnedPhotos[0], photo);
        }

        [TestMethod]
        public void DeletePhoto()
        {
            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(new List<string>());
            GoogleServices.SetupSequence(g => g.DeleteImage(It.IsAny<string>()));

            User user = SessionLogic.SignUp(User());
            Photo photo = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImageInBase64>");
            PhotoLogic.DeleteById(photo.Id);
            Assert.IsFalse(PhotoLogic.ExistsById(photo.Id));
        }

        [TestMethod]
        public void AddTag()
        {
            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(new List<string>());

            User user = SessionLogic.SignUp(User());
            Photo photoToUpdate = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImageInBase64>");
            Tag tag = Tag.ByName("Cat");
            Photo updatedPhoto = PhotoLogic.AddTag(photoToUpdate.Id, tag);

            Assert.IsTrue(updatedPhoto.Tags.Exists(t => t.Name == tag.Name));
        }

        [TestMethod]
        public void DeleteTag()
        {
            List<string> tags = new List<string>();
            tags.Add("Cat");
            GoogleServices.SetupSequence(g => g.UploadImage(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()));
            GoogleServices.SetupSequence(g => g.GetImageURL(It.IsAny<string>()))
                .Returns("https://storage.googleapis.com/example");
            GoogleServices.SetupSequence(g => g.GetImageTags(It.IsAny<string>()))
                .Returns(tags);

            User user = SessionLogic.SignUp(User());
            Photo photoToUpdate = UserLogic.AddPhoto(user.Email, Photo(), "<encodedImageInBase64>");
            Photo updatedPhoto = PhotoLogic.DeleteTag(photoToUpdate.Id, photoToUpdate.Tags[0].Id);

            Assert.IsTrue(updatedPhoto.Tags.Count == 0);
        }
    }
}
