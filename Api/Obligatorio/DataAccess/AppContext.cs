using Domain;
using Microsoft.EntityFrameworkCore;
using System;

namespace DataAccess
{
    public class AppContext : DbContext
    {
        public DbSet<User> Users { get; set; }

        public AppContext(DbContextOptions options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Photo>().HasMany(p => p.Tags).WithOne(t => t.Photo)
             .HasForeignKey("PhotoId")
             .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<AlbumPhoto>()
            .HasKey(t => new { t.AlbumId, t.PhotoId });

            modelBuilder.Entity<AlbumPhoto>()
                .HasOne(ap => ap.Album)
                .WithMany(a => a.AlbumPhotos)
                .HasForeignKey(ap => ap.AlbumId);

            modelBuilder.Entity<AlbumPhoto>()
                .HasOne(ap => ap.Photo)
                .WithMany(p => p.AlbumPhotos)
                .HasForeignKey(ap => ap.PhotoId);
        }
    }
}
