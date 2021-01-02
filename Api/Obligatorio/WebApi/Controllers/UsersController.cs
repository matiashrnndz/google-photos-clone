using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Threading.Tasks;
using BusinessLogic;
using BusinessLogic.Interface;
using DataAccess;
using GoogleAPI;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using WebApi.Models;

namespace WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : Controller
    {
        private IUserLogic Users;

        public UsersController() : base()
        {
            DbContext context = ContextFactory.GetNewContext(ContextType.SQL);
            UserRepository repository = new UserRepository(context);
            Users = new UserLogic(repository, new GoogleServices());
        }

        [HttpPut]
        [Authorize]
        public IActionResult Update([FromBody] UserModel userFromRequest)
        {
            try
            {
                string email = HttpContext.User.Claims.First().Value;
                var user = UserModel.ToModel(Users.Update(email, UserModel.ToEntity(userFromRequest)));
                return Ok(user);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}