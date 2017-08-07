# -*- coding:UTF-8 -*-8
'''
so helper :
including  encrypt libsecidea.so JNI_OnLoad  

'''
import sys,os
import struct
class SoHelper(object):
	def __init__(self):
		print 'call sohelper'

	def call_cmd(self):
		print 'call cmd'


class  ELF32(object):
	def __init__(self):
		print " call elf32"		
	def readELF32Header(sefl,soinfo):
		print 'readelf e_eheader info'
		global elf_class
		global end_char
		fmt_ident='16s'
		fmt_32 ='HHIIIIIHHHHHH'
		fmt_64 ='HHIQQQIHHHHHH'
		elf32_header=[
		          'e_ident',
		          'e_type',
		          'e_version',
		          'e_entry',
		          'e_phoff',
		          'e_shoff',
		          'e_flags',
		          'e_ehsize',
		          'e_phentsize',
		          'e_phnum',
		          'e_shentsize',
		          'e_shnum',
		          'e_shstrndx'
		          ]
		soinfo.seek(0) #adjust file read position
		e_ident = soinfo.read(struct.calcsize(fmt_ident))
		fmt =None
		elf32_e_header_data=None
		# check is elf32 or elf64
		if ord(e_ident[4]) == 1:
			elf_class =32
			fmt = fmt_32
			elf32_e_header_data = soinfo.read(struct.calcsize(fmt_32))
		elif ord(e_ident[4]) == 2:
			elf_class = 64
			fmt = fmt_64
			elf32_e_header_data =  soinfo.read(struct.calcsize(fmt_64))
		#check whether little-endian or big-endian
		if ord(e_ident[5]) == 1: #little-endian
			fmt = '<'+fmt_ident+fmt
			end_char='<'
		elif ord(e_ident[5]) == 2: #big-endian
			fmt ='>'+fmt_ident+fmt
			end_char='>'
		return dict(zip(struct.unpack(fmt,e_ident+elf32_e_header_data)))

	def readSectionHeader(self,soinfo,elf32Header):
		print '[+] read section header \n'
		fmt = '@IIIIIIIIII'
		fmt32 = 'IIIIIIIIII'
		fmt63 = 'IIQQQQIIQQ'
		sh_table = ['sh_name',
					'sh_type',
					'sh_flags',
					'sh_addr',
					'sh_offset',
					'sh_size',
					'sh_link',
					'sh_info',
					'sh_addralign',
					'sh_entsize']
		sh_hdrs =[]
		elf_hdrs = elf32Header
		soinfo.seek(elf_hdrs['e_shoff'])