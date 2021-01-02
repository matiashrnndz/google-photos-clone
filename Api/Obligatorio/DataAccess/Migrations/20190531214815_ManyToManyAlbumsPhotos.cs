using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace DataAccess.Migrations
{
    public partial class ManyToManyAlbumsPhotos : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Photo_Album_AlbumId",
                table: "Photo");

            migrationBuilder.DropIndex(
                name: "IX_Photo_AlbumId",
                table: "Photo");

            migrationBuilder.DropColumn(
                name: "AlbumId",
                table: "Photo");

            migrationBuilder.CreateTable(
                name: "AlbumPhoto",
                columns: table => new
                {
                    PhotoId = table.Column<Guid>(nullable: false),
                    AlbumId = table.Column<Guid>(nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_AlbumPhoto", x => new { x.AlbumId, x.PhotoId });
                    table.ForeignKey(
                        name: "FK_AlbumPhoto_Album_AlbumId",
                        column: x => x.AlbumId,
                        principalTable: "Album",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_AlbumPhoto_Photo_PhotoId",
                        column: x => x.PhotoId,
                        principalTable: "Photo",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_AlbumPhoto_PhotoId",
                table: "AlbumPhoto",
                column: "PhotoId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "AlbumPhoto");

            migrationBuilder.AddColumn<Guid>(
                name: "AlbumId",
                table: "Photo",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Photo_AlbumId",
                table: "Photo",
                column: "AlbumId");

            migrationBuilder.AddForeignKey(
                name: "FK_Photo_Album_AlbumId",
                table: "Photo",
                column: "AlbumId",
                principalTable: "Album",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
