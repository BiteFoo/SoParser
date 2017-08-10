# -*- coding:UTF-8 -*-8
'''
so helper :
including  encrypt libsecidea.so JNI_OnLoad  

'''
import sys,os
import struct
class SoHelper(object):
	def __init__(self):
		print '[+] call sohelper'

	def call_cmd(self):
		print 'call cmd'

class  ELF32(object):
	def __init__(self ,soPath):
		print " call elf32"
		self.sopath = soPath
	def call_encrypt_section(self):
		try:
			soinfo = open(self.sopath,'rb')
			elf32_hdrs = self.readELF32Header(soinfo)
			if elf32_hdrs == None:
				raise  'excetption elf32_hdrs is None'
			print 'elf32_hdrs size ',len(elf32_hdrs)
			elf32_shdrs = self.readSectionHeader(soinfo,elf32_hdrs)						
			if elf32_shdrs == None:
				raise 'exception elf32_shdrs is None '
			elf.encryption_section_header_by_xor(soinfo,elf32_shdrs)
		except Exception as e:
			raise e			
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
			print '32-bit'
		elif ord(e_ident[4]) == 2:
			elf_class = 64
			fmt = fmt_64
			elf32_e_header_data =  soinfo.read(struct.calcsize(fmt_64))
			print '64-bit'
		#check whether little-endian or big-endian
		if ord(e_ident[5]) == 1: #little-endian
			fmt = '<'+fmt_ident+fmt
			end_char='<'
			print 'little-endian'
		elif ord(e_ident[5]) == 2: #big-endian
			fmt ='>'+fmt_ident+fmt
			end_char='>'
			print 'big-endian'
		return dict(zip(elf32_header,struct.unpack(fmt,e_ident+elf32_e_header_data)))

	def readSectionHeader(self,soinfo,elf32Header):
		print '[+] read section header \n'
		fmt = '@IIIIIIIIII'
		fmt32 = 'IIIIIIIIII'
		fmt63 = 'IIQQQQIIQQ'
		sh_table = [
					'sh_name',
					'sh_type',
					'sh_flags',
					'sh_addr',
					'sh_offset',
					'sh_size',
					'sh_link',
					'sh_info',
					'sh_addralign',
					'sh_entsize'
					]
		sh_hdrs =[]
		elf_hdrs = elf32Header
		print 'befor e_shentsize',elf32Header['e_shentsize']
		soinfo.seek(elf_hdrs['e_shoff'])
		print ' e_shnum ',elf_hdrs['e_shnum']
		count =0
		for shentid in range(elf_hdrs['e_shnum']):
			print 'e_shentszie',elf_hdrs['e_shentsize']
			count += 1
			#data =  soinfo.read(elf_hdrs['e_shentsize'])
			#sh_hdrs.append(dict(zip(sh_table,struct.unpack(fmt,data))))
		print 'sh_hdrs size = ',len(sh_hdrs) ,'count  ',count
		sys.exit(0)	
		shstrndx_hdrs = sh_hdrs[elf_hdrs['e_shstrndx']] #??
		soinfo.seek(shstrndx_hdrs['sh_offset']) 
		shstr = soinfo.read(shstrndx_hdrs['sh_size'])
		idx =0
		for hdr in sh_hdrs:
			offset = hdr['sh_name_idx'] #?? 
			hdr['sh_name'] = shstr[offset:offset+shstr[offset:].index(chr(0x0))]
			global shidx_strtab
			if '.strtab' == hdr['sh_name']:
				shidx_strtab =idx
			idx += 1
		print '[+] read section header over ... \n'
		return sh_hdrs
	# .hya  need enrypt <==>	 JNI_OnLoad enryption
	def encryption_section_header_by_xor(self,soinfo,sh_hdrs,sectionName='.hya'):
		print '[+] call enryption ... \n'
		sh_hdr ={}
		for section in sh_hdrs:
			if section['sh_name'] == sectionName:
				sh_hdr = section
				break
		offset = sh_hdr['sh_offset']
		size = sh_hdr['sh_size']
		soinfo.seek(24) #?? why is 24
		soinfo.write(struct.pack('I',size))
		soinfo.seek(32) #? why is 32
		soinfo.write(struct.pack('I',offset))
		soinfo.seek(offset)
		content = soinfo.read(size)
		encontent = self.encrypt(content) # call encrypt 
		soinfo.seek(offset)
		soinfo.write(''.join(encontent))
		print '[+] encrypt complete ... \n'
		soinfo.close()
	def encrypt(self,content):
		print '[+] call encrypt begin..\n'
		encontent =[]
		for data in content:
			encontent.append(chr(ord(data)) ^ 11) # encrypt data ^ 11 
		print '[+] encrypt finish ..\n'
		return encontent 


if __name__ == '__main__':

	if len(sys.argv) != 2:
		print '" usage: python sohelper.py path/xxx.so"'
		sys.exit(0)
	elif len(sys.argv) ==2:
		path = sys.argv[1]

	elf32 = ELF32(path)
	elf32.call_encrypt_section()


		





