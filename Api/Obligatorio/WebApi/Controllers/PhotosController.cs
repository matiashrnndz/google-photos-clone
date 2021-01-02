using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BusinessLogic;
using BusinessLogic.Interface;
using DataAccess;
using Domain;
using GoogleAPI;
using GoogleAPIInterface;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using WebApi.Models;

namespace WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PhotosController : Controller
    {
        private IUserLogic Users;
        private IPhotoLogic Photos;

        public PhotosController() : base()
        {
            DbContext context = ContextFactory.GetNewContext(ContextType.SQL);
            UserRepository repository = new UserRepository(context);
            PhotoRepository photos = new PhotoRepository(context);
            IGoogleServices googleServices = new GoogleServices();
            Users = new UserLogic(repository, googleServices);
            Photos = new PhotoLogic(repository, photos, googleServices);
        }

        [HttpPost]
        [Authorize]
        public IActionResult UploadPhoto([FromBody] PhotoModel photoFromRequest)
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                return Ok(PhotoModel.ToModel(Users.AddPhoto(email, photoFromRequest.ToEntity(), photoFromRequest.ImageBase64)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet]
        [Authorize]
        public IActionResult GetAllPhotos()
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                return Ok(Users.GetAllPhotos(email).ConvertAll(p => PhotoModel.ToModel(p)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [Route("search")]
        [HttpGet]
        [Authorize]
        public IActionResult SearchByTags([FromQuery] string[] tag)
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                List<Tag> tags = new List<Tag>();
                foreach (string t in tag)
                {
                    tags.Add(Tag.ByName(t));
                }
                return Ok(Photos.SearchByTags(email, tags).ConvertAll(p => PhotoModel.ToModel(p)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet("{id}")]
        [Authorize]
        public IActionResult GetPhoto(Guid id)
        {
            try
            {
                PhotoModel model = new PhotoModel(Photos.GetById(id));
                return Ok(model);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Authorize]
        public IActionResult DeletePhoto([FromQuery] Guid[] id)
        {
            try
            {
                foreach (Guid photoId in id)
                {
                    Photos.DeleteById(photoId);
                }
                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPost("{id}/tags")]
        [Authorize]
        public IActionResult AddTag(Guid id, [FromQuery] string tag)
        {
            try
            {
                return Ok(PhotoModel.ToModel(Photos.AddTag(id, Tag.ByName(tag))));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete("{id}/tags/{tagId}")]
        [Authorize]
        public IActionResult DeleteTag(Guid id, Guid tagId)
        {
            try
            {
                return Ok(PhotoModel.ToModel(Photos.DeleteTag(id, tagId)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}