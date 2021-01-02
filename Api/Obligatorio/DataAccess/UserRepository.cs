using DataAccess.Interface.Exceptions;
using Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace DataAccess
{
    public class UserRepository : BaseRepository<User>
    {
        public UserRepository(DbContext context)
        {
            Context = context;
            MigrateDB();
        }

        public override void MigrateDB()
        {
            if (Context.Database.IsSqlServer())
            {
                Context.Database.EnsureCreated();
            }
        }

        public override User Add(User userToAdd)
        {
            try
            {
                User user = Context.Set<User>().Add(userToAdd).Entity;
                Save();
                return user;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override User Update(User userToUpdate)
        {
            try
            {
                Context.Entry(userToUpdate).State = EntityState.Modified;
                Save();
                return userToUpdate;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override bool Exists(Guid id)
        {
            try
            {
                List<User> list = GetAll();
                return list.Exists(x => x.Id == id);
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override User Get(Guid id)
        {
            try
            {
                User user = Context.Set<User>()
                    .Where(x => x.Id == id)
                    .First();
                return user;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override List<User> GetAll()
        {
            try
            {
                return Context.Set<User>()
                    .Include(x => x.Photos).ThenInclude(p => p.Tags)
                    .Include(x => x.Albums)
                    .ThenInclude(a => a.AlbumPhotos)
                    .ThenInclude(ap => ap.Photo)
                    .ThenInclude(p => p.Tags)
                    .ToList();
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }
    }
}