using DataAccess.Interface.Exceptions;
using Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;

namespace DataAccess
{
    public class AlbumRepository : BaseRepository<Album>
    {
        public AlbumRepository(DbContext context)
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

        public override bool Exists(Guid id)
        {
            try
            {
                List<Album> list = GetAll();
                return list.Exists(x => x.Id == id);
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override Album Get(Guid id)
        {
            try
            {
                Album album = Context.Set<Album>()
                    .Where(x => x.Id == id)
                    .Include(x => x.AlbumPhotos).ThenInclude(ap => ap.Photo).ThenInclude(p => p.Tags)
                    .First();
                return album;
            }
            catch (SqlException ex)
            {
                throw new DatabaseError(BDCONNECTIONERROR, ex);
            }
        }

        public override List<Album> GetAll()
        {
            try
            {
                return Context.Set<Album>()
                    .Include(x => x.AlbumPhotos)
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
