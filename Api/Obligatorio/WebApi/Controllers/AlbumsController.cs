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
    public class AlbumsController : ControllerBase
    {
        private IUserLogic Users;
        private IAlbumLogic Albums;

        public AlbumsController() : base()
        {
            DbContext context = ContextFactory.GetNewContext(ContextType.SQL);
            UserRepository repository = new UserRepository(context);
            PhotoRepository photos = new PhotoRepository(context);
            AlbumRepository albums = new AlbumRepository(context);
            IGoogleServices googleServices = new GoogleServices();
            Users = new UserLogic(repository, googleServices);
            Albums = new AlbumLogic(repository, photos, albums);
        }

        [HttpPost]
        [Authorize]
        public IActionResult GenerateAlbum([FromQuery] string[] tag, [FromQuery] string name = "")
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                List<Tag> tags = new List<Tag>();
                foreach (string t in tag)
                {
                    tags.Add(Tag.ByName(t));
                }
                return Ok(AlbumModel.ToModel(Albums.CreateAlbumByTags(email, name, tags)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet]
        [Authorize]
        public IActionResult GetAllAlbums()
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                return Ok(Users.GetAllAlbums(email).ConvertAll(p => AlbumModel.ToModel(p)));
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete("{id}")]
        [Authorize]
        public IActionResult DeleteAlbum(Guid id)
        {
            try
            {
                Albums.DeleteAlbum(id);
                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}
